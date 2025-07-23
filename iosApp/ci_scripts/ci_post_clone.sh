#!/bin/sh
echo -n "$GOOGLE_SERVICE_PLIST_BASE64" | base64 --decode -o $GOOGLE_SERVICE_PATH