# Time Machine Jenkins plugin

![logo](image.jpg)


This plugin help you track changes on Jenkins configuration. It records changes made from jenkins into git
commits and store them in a local git reporistory, logging author and trying to guess user intent.

This is sort of a combination between JobConfigHistory, scm-sync-configuration and audit-trail plugins

## TODO 

 - [ ] Option to push repo to a remote as a scheduled task
 - [ ] Detect more URI patterns to guess user intent
 - [ ] Detect more stacktrace elements to guess SYSTEM updates causes
 - [ ] UI to list changes and (reusing some Github UI clone ?) 
 - [ ] Option to revert changes from UI