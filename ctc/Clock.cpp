#include "Clock.h"
#include <time.h>
time_t Clock::time()
{
    return std::time(0);
}