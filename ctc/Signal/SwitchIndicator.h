package scrt.ctc.Signal;

import scrt.event.SCRTEvent;
#pragma once;
#include "Signal.h"
#include "ctc/Station.h"
/*namespace ctc::Signal
{*/
class SwitchIndicator : public Signal
{
	public:
	SwitchIndicator(Station *s) : base(s) {}
};
//}
