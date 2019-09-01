#pragma once
#include <functional>
class TrackItem;
typedef std::function<bool(TrackItem*,Orientation,TrackItem*)> conditionfun;
struct TrackComparer
{
	conditionfun condition;
	conditionfun criticalCondition;
};