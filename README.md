Since this plugin is currently private, here's how to add it to our projects.

1) Clone the repository from GitHub and do a ```Maven -> install```
2) In your other plugins, use the following to add it:
```
<dependency>
    <groupId>com.github.mittenmc</groupId>
    <artifactId>ServerUtils</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```
 - Since the scope is ```provided``` we will run this plugin on the server
3) Add this to your ```depend:``` section in each plugin's plugin.yml
