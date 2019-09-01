#include "Signal.h"
#include "MainSignal.h"
#include "EoT.h"
#include "FixedSignal.h"
#include "ExitIndicator.h"
Signal::Signal(Station *s) : station(s)
{
	station->Signals.push_back(this);
}
void Signal::setLinked(TrackItem *t)
{
	Linked = t;
	Linked->SignalLinked = this;
	send(PacketType::SignalRegister);
	Linked->setSignal(this);
	send(PacketType::SignalData);
}
void Signal::send(PacketType type)
{
    Packet *p;
    switch(type)
	{
		case PacketType::SignalRegister:
		{
			SignalRegister *reg = new SignalRegister(static_pointer_cast<SignalID>(getID()));
			reg->Fixed = isFixed;
			reg->EoT = isEoT;
			p = reg;
		}
			break;
		case PacketType::SignalData:
		{
			SignalData *d = new SignalData(static_pointer_cast<SignalID>(getID()));
			d->Automatic = Automatic;
			d->SignalAspect = SignalAspect;
			d->OverrideRequest = OverrideRequest;
			d->ClearRequest = ClearRequest;
			if(isMainSignal)
			{
				MainSignal *ms = (MainSignal*)this;
				d->UserRequest = ms->UserRequest;
				d->MT = ms->MT;
				d->stickClose = ms->stickClose;
			}
			p = d;
		}
			break;
		default:
			return;
		}
		COM::toSend(p);
}
Signal* Signal::construct(SignalRegister *reg)
{
	SignalID *id = (SignalID*) reg->id.get();
	switch(id->Class)
	{
		case Exit_Indicator:
			return new ExitIndicator(id->Name, Station::byNumber(id->stationNumber));
		default:
			if(reg->EoT) return new EoT(id->Direction, Station::byNumber(id->stationNumber));
			if(reg->Fixed) return new FixedSignal(id->Name, id->Direction, Anuncio_parada, Station::byNumber(id->stationNumber));
			return new MainSignal(id->Name, Station::byNumber(id->stationNumber));
	}
}
shared_ptr<ID> Signal::getID()
{
	if(!idCreated)
	{
		id.Class = Class;
		id.Direction = Direction;
		id.stationNumber = station->AssociatedNumber;
		id.Number = Number;
		id.Track = Track;
		id.Name = Name;
		idCreated = true;
	}
	return shared_ptr<SignalID>(new SignalID(id));
}