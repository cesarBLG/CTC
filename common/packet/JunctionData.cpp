#include "JunctionData.h"
JunctionData::JunctionData(shared_ptr<JunctionID> id) : StatePacket(id, PacketType::JunctionData, PacketGroup::Data) {}
vector<char> JunctionData::getListState()
{
    vector<char> data = id->getId();
    data.push_back((int)BlockState);
    data.push_back(shunt);
    data.push_back((int)Occupied);
    data.push_back((int)Switch);
    data.push_back(Locked);
    data.push_back(blockPosition);
    data.push_back(locking);
    return data;
}
JunctionData *JunctionData::byState(char *&data)
{
    JunctionData *jd = new JunctionData(shared_ptr<JunctionID>(new JunctionID(data)));
    jd->BlockState = (Orientation)*data++;
    jd->shunt = *data++;
    jd->Occupied = (Orientation)*data++;
    jd->Switch = (Position)*data++;
    jd->Locked = *data++;
    jd->blockPosition = *data++;
    jd->locking = *data++;
    return jd;
}