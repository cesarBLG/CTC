#include "TCP.h"
#include "com/COM.h"
#include "ClientListener.h"
#ifdef WIN32
#include <winsock2.h>
#else
#include <unistd.h>
#include <arpa/inet.h>
#endif
void TCP::initialize()
{
#ifdef WIN32
	WSADATA wsa;
	WORD ver = MAKEWORD(2, 2);
	WSAStartup(ver, &wsa);
#endif
	thread thr([this]() {
		int server = socket(AF_INET, SOCK_STREAM, 0);
		struct sockaddr_in serv;
		serv.sin_family = AF_INET;
		serv.sin_port = htons(5300);
		serv.sin_addr.s_addr = INADDR_ANY;
		bind(server, (struct sockaddr *)&(serv), sizeof(serv));
		listen(server, 10);
		while (1)
		{
			struct sockaddr_in addr;
			int c = sizeof(struct sockaddr_in);
			int cl = accept(server, (struct sockaddr *)&addr,
#ifndef WIN32
							(socklen_t *)
#endif
							&c);
			ClientListener *client = new ClientListener(cl);
			COM::addDevice(client);
		}
	});
	thr.detach();
}