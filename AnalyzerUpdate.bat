echo off

IF NOT EXIST commodities.jar (
   echo No update available
   goto out
)

jar xf commodities.jar

del commodities.jar

:out
echo done