#include "JunctionID.h"
JunctionID::JunctionID() : ID(ElementType::Junction) {}
JunctionID::JunctionID(char *&data) : ID(data)
{
    Number = *data++;
    Name = "A" + to_string(Number);
}
vector<char> JunctionID::getId()
{
    vector<char> l = ID::getId();
    l.push_back(Number);
    return l;
}
ID *JunctionID::byState(char *&data)
{ 
    return new JunctionID(data);
}