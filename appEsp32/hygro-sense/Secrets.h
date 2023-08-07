#include <pgmspace.h>
 
#define SECRET
#define THINGNAME "hygrosense"

const char AWS_IOT_ENDPOINT[] = "anywz7kwswoml-ats.iot.eu-north-1.amazonaws.com";

// Amazon Root CA 1
static const char AWS_CERT_CA[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----

Insert Amazon Root CA 1 certificate here

-----END CERTIFICATE-----
)EOF";
 
// Device Certificate
static const char AWS_CERT_CRT[] PROGMEM = R"KEY(
-----BEGIN CERTIFICATE-----

Insert AWS IoT Core device certificate here

-----END CERTIFICATE-----
 
 
)KEY";
 
// Device Private Key
static const char AWS_CERT_PRIVATE[] PROGMEM = R"KEY(
-----BEGIN RSA PRIVATE KEY-----

Insert AWS private key here

-----END RSA PRIVATE KEY-----
 
 
)KEY";