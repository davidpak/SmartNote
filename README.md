# SmartNote

SmartNote is an automated note-taking organization application that aims to revolution of students engage with educational content. SmartNote automates the process of creating detailed notes from PDFs, slideshows, and audio transcripts. It also provides a simple way to transfer the generated notes to the user's Notion workspace.

## Goals

- Create detailed, formatted notes from PDFs (e.g. lecture slides, research papers)
- Create detailed, formatted notes from video/audio transcripts (e.g. lecture recordings)
- Connect to the user's Notion workspace to transfer the generated notes
  - Conversion into Notion-friendly structure w/ headings, lists, etc.
- Generation Customization (Inputting specialized AI prompts and toggles for specific settings)
- Output categories identified from the document and allow the user to select which ones should be included or discluded in the generated notes

## Layout

- `client/` - Client resources
  - `src/` - Client source code
    - `assets/` - Images and other assets
    - `components/` - React components
    - `utils/` - Utility functions
- `server/` - Server resources
  - `examples/` - Example documents
  - `main/` - Server sources
    - `src/` - Main runtime sources
    - `test/` - Test sources
  - `scripts/` - Scripts used by the server

## Building

### Client

The client uses [Node.js](https://nodejs.org/en/) and React. Ensure you have installed these before continuing. You may test your installation by running `node --version` and `npm --version` in a terminal.

Unless otherwise specified, all commands should be run from the `client/` directory.

Dependencies are specified in `package.json`. To download them, use:

`npm install`

Then, to build the client, do:

`npm run build`

### Server

The server uses [Apache Ant](https://ant.apache.org/index.html) as its build system and [Apache Ivy](https://ant.apache.org/ivy/) as its dependency manager. Ensure you have installed these before continuing (or you are using an IDE with built-in support). For Ivy, `ivy-x.x.x.jar` must be somewhere within Ant's library path (usually in the library directory at `~/.ant/lib/` or `ANT_HOME/lib/`). You may test your installation by running `ant -version` in a terminal. An installation of [Java](https://www.oracle.com/java/technologies/downloads/) 1.8 or higher is required. You may test your installation by running `java -version` in a terminal.

[JUnit 4](https://junit.org/junit4/) is used as the testing framework. It should be downloaded when running `ant resolve`, but if not, download it and ensure that `junit-4.x.jar` is on the classpath. You may need to add libraries to Ant's library path. See how [Ant handles JUnit](https://ant.apache.org/manual/Tasks/junit.html) for more information.

Make sure your working directory is the `server/` directory before running any commands. The following commands are important:

- `ant resolve` - Checks for and downloads dependencies.
- `ant build` - Builds the server. This will download dependencies if they have not already been downloaded.
- `ant jar` - Creates a JAR file of the server. This will build the server if it has not already been built.
- `ant dist` - Creates a standalone distributable archive of the server. This will build the server if it has not already been built.
- `ant build-test` - Builds the server and tests.

You may also set up an IDE to build and run the server. Ensure your IDE has support for Ant and set up your project to use the buildfile `server/build.xml`, have configured it to use a suitable JRE, and to include all of the `jar` files in the `server/lib/` directory on the classpath.

## Running

First, be sure to build the client and server. See the previous section for instructions.

### Client

From within the `client/` directory, do:

`npm run dev`

### Server

Make sure your working directory is the `server/` directory before running any commands. The following commands are important:

- `ant run` - Runs the server. This will build the server if it has not already been built. Specify `--Dargs="..."` to pass arguments to the server. This will only run the compiled classes, not the JAR file or distributable archive.
- `ant test` - Runs the server tests. This will build the server and the tests if they have not already been built.

If you have built a JAR file of the server, you may run either `server.bat [args...]` (Windows) or `server.sh [args...]` (Mac/Linux) from the `server/` directory.

When using an IDE, you may need to setup run configurations to target buildfile `server/build.xml`. Some IDEs may be able to build without Ant, but you may need to set up the classpath within your IDE's configuration. It must include all of the `jar` files in the `server/lib/` directory. Ensure your IDE has set the working directory to the `server/` directory.

## Third-Party Libraries

- [Framer Motion](https://www.framer.com/motion/) - Used for animations.
- [Gson](https://github.com/google/gson) - Used for JSON serialization and deserialization.
- [java-jwt](https://github.com/auth0/java-jwt) - Used for JSON Web Token (JWT) creation and verification.
- [JUnit 4](https://junit.org/junit4/) - Used for testing server.
- [React](https://react.dev/) - Used for creating user interface components.
- [React Icons](https://react-icons.github.io/react-icons/) - Used for SVG icons.
- [Spark](https://sparkjava.com/) - Used as the backend web framework.
- [Tailwind CSS](https://tailwindcss.com/) - Used for CSS.
