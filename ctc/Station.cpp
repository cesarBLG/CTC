#include "Station.h"
#include "Signal/MainSignal.h"
vector<Station*> Station::stations;
void Station::Open()
{
	if(Opened||AssociatedNumber==0) return;
	Opened = true;
	for(Signal *s : Signals)
	{
		if(s->isMainSignal) ((MainSignal*)s)->setState();
	}
}
void Station::Close()
{
	if(!Opened) return;
	Opened = false;
    for(Signal *s : Signals)
	{
		if(s->isMainSignal) ((MainSignal*)s)->setState();
	}
}