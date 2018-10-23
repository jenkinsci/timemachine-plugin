# Time Machine Jenkins plugin

![logo](image.jpg)


This plugin help you track changes on Jenkins configuration. It records changes 
made to jenkins configuration as commits in a local git repository, logging author 
os one can easily audit who changed what on a master.

Final goal is to provide sort of a combination of JobConfigHistory, scm-sync-configuration and audit-trail plugins

## Status

The basic change collection infrastructure is implemented.
JENKINS_HOME is converted into a git repository, changes are tracked as commits
An UI is provided to list changes (i.e. commits) and diff 
 
## TODO 

 - [ ] Track item creation so we can log name and type
 - [ ] Option to push repo to a remote as a scheduled task
 - [ ] Option to revert changes from UI