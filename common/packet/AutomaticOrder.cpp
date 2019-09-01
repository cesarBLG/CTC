#include "AutomaticOrder.h"
AutomaticOrder::AutomaticOrder(shared_ptr<SignalID> id) : StatePacket(id, PacketType::AutomaticOrder, PacketGroup::Order) {}
vector<char> AutomaticOrder::getListState()
{
    vector<char> data = id->getId();
    data.push_back(booleanConvert(automatic, stickClose));
    return data;
}
Packet *AutomaticOrder::byState(char *&data)
{
    AutomaticOrder *ao = new AutomaticOrder(shared_ptr<SignalID>(new SignalID(data)));
    ao->automatic = *data & 1;
    ao->stickClose = *data & 2;
    data++;
    return ao;
}