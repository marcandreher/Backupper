
# Backupper


## Demo
![Backupper](https://i.imgur.com/4WFdR7P.gif "Backupper")

## Logfile
![Logfile](https://i.imgur.com/2LPsLqD.gif "Logfile")

Backupper is a easy to use and modular compression tool to save your folders, files and your databases to a zip files. To Download the current Release check out the [Release Page](https://www.google.com "Google's Homepage")
#### What is the boilerplate of this
 - Adding folders to your backup action
 - Adding files to your backup action
 - Adding MySQL tables to your backup action
 - Logging errors to your matching log file


## Setup

Clone the project

```bash
  git clone https://github.com/marcandreher/Backupper
```

Go to the project directory

```bash
  cd Backupper
```

Run it to generate the config

```bash
  java -jar Backupper.jar
```
Edit the config in `config.json`
```json
{
  "mySQLEnabled" : false,
  "mySQLUserName" : "",
  "mySQLPassword" : "",
  "mySQLAdress" : "",
  "mySQLDumpLoc" : "mysqldump",
  "mySQLPort" : 3306,
  "cronEnabled" : true,
  "actionFolder" : "actions/",
  "outputFolder" : "output/"
}
```

Generate a test action to edit it

```
java -jar Backupper.jar -gen
```

Edit the test action saved in `actions/gen.json`

```json
{
  "name" : "Example Name",
  "filepaths" : [ ],
  "toexportdb" : [ ],
  "database" : "mysql",
  "interval" : "1d",
  "exportname" : "%date%-%name%",
  "compressing" : "zip",
  "decaying" : "4w"
}
```
`name` - Name of action (Make sure to use valid filenames if you use it as `exportname`)

`filepaths` - Files and folders that should be saved (optional)
```json
  "filepaths" : [ 
      "/home/test.txt",
      "/home/myDocs"
   ],
```

`toexportdb` - Any SQL Dump Files or MySQL whole database/table, `*` means whole database (optional)

`database` - Which database is used (optional)

```json
  "toexportdb" : [ 
      "/home/db.sql",
      "phpmyadmin",
      "mytable"
   ],
```
`exportname` - Name used in logs, as exportname and in folders

`compressing` - Output compression currently only zip

`interval` - Comming soon (optional)

`decaying` - Comming soon (optional)

## Run Arguments

`java -jar Backupper.jar -a <name>` - Start a action

`java -jar Backupper.jar -all` - Start all actions

`java -jar Backupper.jar -gen` - Generate a test action

`java -jar Backupper.jar` - Setup config & firstrun


## Contributing

Contributions are always welcome!
