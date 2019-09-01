#include "Axle.h"
#include "Wagon.h"
void Wagon::addAxle(Axle *e)
{
	length+=30;
	e->wagon = this;
	axles.push_back(e);
}