# SmartNote
SmartNote revolutionizes student engagement with LangChain, TypeScript, and Notion API. It automates detailed note creation from videos and PDFs, seamlessly integrating into the user’s Notion workspace.

## Layout

* `client/` - Client resources
  * `src/` - Client web source code
* `server/` - Server resources
  * `src/` - Server source code
  * `scripts/` - Scripts used by the server

## Building

### Client

The client uses [Node.js](https://nodejs.org/en/) and React. Ensure you have installed these before continuing. You may test your installation by running `node --version` and `npm --version` in a terminal.

Unless otherwise specified, all commands should be run from the `client/` directory.

Dependencies are specified in `package.json`. To download them, use:

`npm install`

Then, to build the client, do:

`npm run build`

### Server

The server uses [Apache Ant](https://ant.apache.org/index.html) as its build system and [Apache Ivy](https://ant.apache.org/ivy/) as its dependency manager. Ensure you have installed these before continuing (or you are using an IDE with built-in support). For Ivy, `ivy-x.x.x.jar` must be somewhere within Ant's library path (usually in the user library directory at `~/.ant/lib/`). You may test your installation by running `ant -version` in a terminal. An installation of [Java](https://www.oracle.com/java/technologies/downloads/) 1.8 or higher is required. You may test your installation by running `java -version` in a terminal.

Make sure your working directory is the `server/` directory before running any commands. The following commands are important:

* `ant resolve` - Downloads dependencies.
* `ant build` - Builds the server. This will download dependencies if they have not already been downloaded.
* `ant dist` - Creates a standalone distributable, which can be run without Ant. This will build the server if it has not already been built.

You may also set up an IDE to build and run the server. Ensure your IDE has support for Ant and set up your project to use the `build.xml` file in the `server/` directory and have configured the correct JDK.

## Running

First, be sure to build the client and server. See the previous section for instructions.

### Client

From within the `client/` directory, do:

`npm run dev`

### Server

Make sure your working directory is the `server/` directory before running any commands. Specify `--Dargs="..."` in the `ant` command to pass arguments to the server. To run the server, do:

`ant run`

This will not run the standalone distributable.

When using an IDE, you may need to setup run configurations to target the `build.xml` file in the `server/` directory. Some IDEs may be able to build without Ant, but you may need to set up the classpath within your IDE's configuration. It must include all of the `jar` files in the `server/lib/` directory. Ensure your IDE has set the working directory to the `server/` directory.

## Third-Party Libraries

* [Spark](https://sparkjava.com/) - Used as the backend web framework.
* [Gson](https://github.com/google/gson) - Used for JSON serialization and deserialization.
