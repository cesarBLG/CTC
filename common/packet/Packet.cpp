#include "Packet.h"
#include "ACData.h"
#include "AutomaticOrder.h"
#include "ClearOrder.h"
#include "ConfigPacket.h"
#include "Device.h"
#include "ItineraryStablisher.h"
#include "JunctionData.h"
#include "JunctionLock.h"
#include "JunctionPositionSwitch.h"
#include "JunctionRegister.h"
#include "JunctionSwitch.h"
#include "LinkPacket.h"
#include "RequestPacket.h"
#include "SignalData.h"
#include "SignalRegister.h"
#include "StationRegister.h"
#include "TrackData.h"
#include "TrackRegister.h"
char getControl(const char* data, int length, int offset)
{
	int control = 0;
	for(int i=offset; i<length - 1; i++)
    {
		control += data[i] * (((i-offset) % 4) == 0 ? 4 : ((i-offset) % 4));
    }
	control = 255 - (control % 255);
	return (char)control;
}
Packet::Packet(PacketType t, PacketGroup g) : type(t), group(g) {}
Packet *Packet::construct(Device *dev)
{
	char magic[4];
	dev->read(magic, 4);
	while (magic[0] != 'S' || magic[1] != 'C' || magic[2] != 'R' || magic[3] != 'T')
	{
		int i = 0;
		for (i = 0; i < 3; i++)
		{
			magic[i] = magic[i + 1];
		}
		magic[i] = dev->read();
	}
	char length = dev->read();
	char data[length];
	dev->read(data, length);
	if (data[length - 1] != getControl(data, length, 0))
		return nullptr;
	PacketType type = (PacketType)data[0];
	/*if(type==PacketType::AutomaticOrder)
	{
		for(int i=0;i<length;i++)
		{
			cout<<(int)data[i]<<" ";
		}
		cout<<endl;
	}*/
	char *f = data;
	Packet *p = factory[type]->byState(++f);
	return p;
}
vector<char> Packet::getState()
{
	vector<char> l = getListState();
	int len = l.size() + 7;
	vector<char> data;
	data.push_back('S');
	data.push_back('C');
	data.push_back('R');
	data.push_back('T');
	data.push_back((char)(len - 5));
	data.push_back((char)type);
	data.insert(data.end(), l.begin(), l.end());
	data.push_back(getControl(&data[0], len, 5));
	return data;
}
vector<char> Packet::toList(string s)
{
	vector<char> l;
	for (int i = 0; i < s.size(); i++)
	{
		l.push_back(s[i]);
	}
	l.push_back(0);
	return l;
}
string Packet::toString(char *&data)
{
	string s = data;
	data += s.length() + 1;
	return s;
}
void Packet::send(set<Device*> devs)
{
    auto v = getState();
    for(auto dev : devs) dev->write(v);
}
map<PacketType, Packet *> Packet::factory;
void Packet::start_factory()
{
	factory[PacketType::LinkPacket] = new LinkPacket();
	factory[PacketType::SignalData] = new SignalData(nullptr);
	factory[PacketType::SignalRegister] = new SignalRegister(nullptr);
	factory[PacketType::TrackData] = new TrackData(nullptr);
	factory[PacketType::TrackRegister] = new TrackRegister(nullptr);
	factory[PacketType::JunctionData] = new JunctionData(nullptr);
	factory[PacketType::JunctionSwitch] = new JunctionSwitch(nullptr);
	factory[PacketType::JunctionRegister] = new JunctionRegister(nullptr, nullptr);
	factory[PacketType::JunctionLock] = new JunctionLock(nullptr);
	factory[PacketType::ItineraryStablisher] = new ItineraryStablisher(nullptr, nullptr);
	factory[PacketType::ACData] = new ACData(nullptr);
	factory[PacketType::RequestPacket] = new RequestPacket();
	factory[PacketType::StationRegister] = new StationRegister(0);
	factory[PacketType::ClearOrder] = new ClearOrder(nullptr);
	factory[PacketType::AutomaticOrder] = new AutomaticOrder(nullptr);
	factory[PacketType::JunctionPositionSwitch] =
		new JunctionPositionSwitch(nullptr, Posibilities::Comprobation);
	factory[PacketType::ConfigPacket] = new ConfigPacket();
}