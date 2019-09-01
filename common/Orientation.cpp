#include "Orientation.h"
Orientation operator!(Orientation dir)
{
	if(dir == Orientation::Even) return Orientation::Odd;
	if(dir == Orientation::Odd) return Orientation::Even;
	return dir;
}