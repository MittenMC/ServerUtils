# ServerUtils
A utility plugin for all of my plugins

### Using as a Dependency
Now that this repository is public, you have the option to use JitPack. The old way is still listed below:

1) Clone the repository from GitHub and do a `Maven -> install`
2) In your other plugins, use the following to add it:
```xml
<dependency>
    <groupId>com.github.mittenmc</groupId>
    <artifactId>ServerUtils</artifactId>
    <version>1.0.6</version>
    <scope>provided</scope>
</dependency>
```
 - Since the scope is `provided` we will run ServerUtils on the server so everything links to the same place
3) Add this to your `depend` section in each plugin's plugin.yml
