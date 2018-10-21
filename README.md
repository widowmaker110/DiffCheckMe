# DiffCheckMe

[![N|Solid](https://www.diffchecker.com/static/images/logo.png)](https://diffchecker.com/)

# Disclaimer
  - I do not in any way, shape, or form, claim rights or association with DiffChecker.com. This project is meant to be a development tool primarily for Salesforce Developers who haven't migrated to [Salesforce DX] just yet.


### Installation
  - Download the Jar file "DiffCheckme.jar" to a local directory
  - Download the DiffCheckMe.properties file
  -- Define the Source directory (e.g. Dev)
  -- Define the Target directory (e.g. QA Full Sandbox)
  -- Define if you want metadata evaluated (true) or not (false)
  -- Define the duration of the Diff. Options are day, month, forever

### Running Program

DiffChecker's API requires [Node.js](https://nodejs.org/) to run.Follow their [CLI] instructions before using this software.

Ensure the properties file is in the same directory as the jar.

Install the dependencies and devDependencies and start your command prompt.

```sh
$ java -jar DiffCheckMe.jar
```

Of you can just click the runnable Jar file.

### Todos

 - Write MORE Tests

License
----

MIT

   [CLI]: <https://www.diffchecker.com/cli>
   [Salesforce DX]: <https://developer.salesforce.com/platform/dx>
