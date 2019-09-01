#include "ACData.h"
ACData::ACData(shared_ptr<ACID> id) : StatePacket(id, PacketType::ACData, PacketGroup::Data) {}
vector<char> ACData::getListState()
{
    vector<char> data = id->getId();
    data.push_back((char)dir);
    return data;
}
Packet *ACData::byState(char *&data)
{
    ACData *ac = new ACData(shared_ptr<ACID>(new ACID(data)));
    ac->dir = (Orientation)*data++;
    return ac;
}