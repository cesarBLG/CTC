#pragma once
#include "packet/Device.h"
#include "COM.h"
#include <istream>
class DevStream : public Device
{
    istream &str;
    public:
    DevStream(istream &is) : str(is) {}
    int read() override
    {
        return str.get();
    }
    void read(char *buff, int count) override
    {
        str.read(buff, count);
    }
    void parse() override
    {
        COM::parse(this);
    }
};