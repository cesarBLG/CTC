/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
#pragma once
#include "ID.h"
#include <vector>
class TrackItemID : public ID
{
	public:
	int x;
	int y;
	TrackItemID() : ID(ElementType::TrackItem) {}
	TrackItemID(char *&data) : ID(data)
	{
		x = *data++;
		y = *data++;
	}
	vector<char> getId() override
	{
		vector<char> l = ID::getId();
		l.push_back(x);
		l.push_back(y);
		return l;
	}
	ID* byState(char *&data) override {return new TrackItemID(data);}
};
