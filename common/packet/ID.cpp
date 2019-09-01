#include "ID.h"
#include "SignalID.h"
#include "JunctionID.h"
#include "TrackItemID.h"
#include "ACID.h"
vector<char> ID::getId()
{
	vector<char> l;
	l.push_back((char)type);
	l.push_back(stationNumber);
	return l;
}
ID* ID::create(char *&data)
{
	ID *id = nullptr;
	ElementType t = (ElementType)*data;
	if(t==ElementType::Signal) id = new SignalID(data);
	else if(t==ElementType::TrackItem) id = new TrackItemID(data);
	else if(t==ElementType::Junction) id = new JunctionID(data);
	else if(t==ElementType::AC) id = new ACID(data);
	return id;
}
ID::ID(ElementType t, int station) : type(t), stationNumber(station) {}
ID::ID(char *&data)
{
	type = (ElementType)*data++;
	stationNumber = *data++;
}
bool ID::equals(ID* id)
{
	return id!=nullptr && id->getId() == getId();
}