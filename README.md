# Heslington Hustle

Assessment 2 implementation of "Heslington Hustle" by Group 2 (The JVMs).

## Running the game

Download the jar for the latest version from the [releases page](https://jjzsilva9.github.io/uoy-eng1-g2.github.io/releases).

Alternatively you can clone the repository and build a jar yourself with the following commands:

```shell
git clone https://github.com/jjzsilva9/assessment2.git
cd assessment2
./gradlew desktop:dist
```

The built jar file can then be found at `desktop/build/libs/desktop-VERSION.jar`.

To run the game once you have the jar file you can use the following command:

```shell
java -jar path/to/game/jar.jar
```

### System property enabled features

The game comes with two debug modes which must be manually enabled by passing the appropriate flag when running
the game.

The first debug mode draws hitbox outlines around both the player and interaction locations, and can be enabled by
setting the `game.debug` system property to `true`. The second debug mode draws outlines around all physics objects
in the game (think: buildings/terrain, world boundaries, player), and can be enabled by setting the `game.physicsDebug`
system property to `true`.

These properties can be set in the run command shown above as follows:

```shell
java -jar -Dproperty.name=value path/to/game/jar.jar
```

For example

```shell
java -jar -Dgame.debug=true path/to/game/jar.jar
```

## Additional information

This repository has a CI pipeline enabled that checks the formatting of any commits - and will fail if
any of the code is not formatted correctly. To run reformatting of all java files locally you should use the 
following command before committing and pushing:

```shell
./gradlew spotlessApply
```

The same pipeline will also run the test suite for the project and fail if any of the tests fail.

### Release pipeline

Creating a github release with the built jar file is automated and just has to be triggered manually. Upon [running
the pipeline manually](https://github.blog/changelog/2020-07-06-github-actions-manual-triggers-with-workflow_dispatch/)
a release will automatically be built from the current state of the code. Once the release has been made the CI will
update the project version and commit it back to the repository. This means you will need to pull before making any
additional code changes.

If for some reason you cannot trigger the workflow (often when running for the first time), you may need to make a
commit adding the `push` event trigger to the workflow file. Once the workflow has finished running for the first
time you can safely remove the trigger. See below for more info.

#### `.github/workflows/release.yaml`

```diff
- on: [workflow_dispatch]
+ on: [push, workflow_dispatch]
```

Once the workflow has finished you should change the line back to what it was before.
