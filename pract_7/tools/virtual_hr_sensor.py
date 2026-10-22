# Виртуальный BLE-пульсометр для эмулятора: регистрирует GATT-устройство с Heart Rate Service
import sys, time, random
sys.path.insert(0, sys.argv[0].rsplit('/', 1)[0] + '/gen')  # сгенерированные gRPC-стабы, см. RUN.md
from concurrent import futures
import grpc
from google.protobuf import empty_pb2
import emulated_bluetooth_pb2 as bt, emulated_bluetooth_pb2_grpc as bt_rpc
import emulated_bluetooth_device_pb2 as dev, emulated_bluetooth_device_pb2_grpc as dev_rpc
import grpc_endpoint_description_pb2 as ep

HR_SERVICE, HR_MEASUREMENT = 0x180D, 0x2A37
CB_ID = dev.Uuid(id=0x2A37)

def measurement(i):
    bpm = 68 + int(6 * random.random()) + (i % 5)
    if i % 4 == 3:
        # иногда шлём формат UINT16 (флаг 0x01), чтобы проверить оба варианта разбора
        return bytes([0x01, bpm & 0xFF, bpm >> 8]), bpm
    return bytes([0x00, bpm]), bpm

class Device(dev_rpc.GattDeviceServiceServicer):
    def OnCharacteristicReadRequest(self, req, ctx):
        data, bpm = measurement(0)
        print('read ->', bpm, flush=True)
        return dev.CharacteristicValueResponse(status=dev.CharacteristicValueResponse.GATT_STATUS_SUCCESS, data=data)
    def OnCharacteristicWriteRequest(self, req, ctx):
        return dev.CharacteristicValueResponse(status=dev.CharacteristicValueResponse.GATT_STATUS_SUCCESS)
    def OnCharacteristicObserveRequest(self, req, ctx):
        print('observe from', req.from_device.address, flush=True)
        i = 0
        while ctx.is_active():
            data, bpm = measurement(i)
            print('notify ->', bpm, data.hex(), flush=True)
            yield dev.CharacteristicValueResponse(status=dev.CharacteristicValueResponse.GATT_STATUS_SUCCESS, data=data)
            i += 1
            time.sleep(1)
    def OnConnectionStateChange(self, req, ctx):
        print('connection', req.new_state, req.from_device.address, flush=True)
        return empty_pb2.Empty()

server = grpc.server(futures.ThreadPoolExecutor(max_workers=8))
dev_rpc.add_GattDeviceServiceServicer_to_server(Device(), server)
port = server.add_insecure_port('127.0.0.1:9658')
server.start()

C = dev.GattCharacteristic
profile = dev.GattProfile(services=[dev.GattService(
    uuid=dev.Uuid(id=HR_SERVICE), service_type=dev.GattService.SERVICE_TYPE_PRIMARY,
    characteristics=[C(uuid=dev.Uuid(id=HR_MEASUREMENT),
                       properties=C.PROPERTY_READ | C.PROPERTY_NOTIFY,
                       permissions=C.PERMISSION_READ, callback_id=CB_ID)])])
device = dev.GattDevice(
    endpoint=ep.Endpoint(target='127.0.0.1:%d' % port),
    advertisement=dev.Advertisement(device_name=sys.argv[1] if len(sys.argv) > 1 else 'Пульсометр (виртуальный)',
                                    connection_mode=dev.Advertisement.CONNECTION_MODE_UNDIRECTED,
                                    discovery_mode=dev.Advertisement.DISCOVERY_MODE_GENERAL),
    profile=profile)
stub = bt_rpc.EmulatedBluetoothServiceStub(grpc.insecure_channel('localhost:8658'))
print('registered:', stub.registerGattDevice(device), flush=True)
server.wait_for_termination()
