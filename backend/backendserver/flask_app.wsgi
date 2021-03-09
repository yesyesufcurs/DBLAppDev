#! /usr/bin/python3.9

import logging
import sys
logging.basicConfig(stream=sys.stderr)
sys.path.insert(0, '/home/vincent/DBLAppDev/backend/backendserver')
from backendserver import app as application