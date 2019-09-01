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
#include <vector>
using namespace std;
enum struct ElementType
{
	Signal,
	AC,
	TrackItem,
	Junction
};
class ID
{
	public:
	ElementType type;
	int stationNumber;
	virtual vector<char> getId();
	protected:
	ID(){}
	public:
	ID(ElementType t, int station=0);
	ID(char *&data);
	virtual ~ID(){}
	bool equals(ID* id);
	virtual ID* byState(char *&data) = 0;
	static ID* create(char *&data);
};
