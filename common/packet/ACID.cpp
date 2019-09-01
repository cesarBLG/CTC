#include "ACID.h"
ACID::ACID() : ID(ElementType::AC) {}
ACID::ACID(char *&data) : ID(data)
{
    Num = *data++;
    dir = Num % 2 == 0 ? Orientation::Even : Orientation::Odd;
}
vector<char> ACID::getId()
{
    vector<char> l = ID::getId();
    l.push_back(Num);
    return l;
}
ID *ACID::byState(char *&data)
{ 
    return new ACID(data); 
}