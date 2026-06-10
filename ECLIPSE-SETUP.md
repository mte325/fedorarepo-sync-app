# Eclipse 2023-03 Setup Guide - Java 1.8

Complete step-by-step guide to download, configure, and run the Fedora Repository Sync App in Eclipse IDE 2023-03 on Windows 11 with Java 1.8.

## System Requirements

Before starting, ensure you have:
- **Java 1.8 (Java 8)** installed with JAVA_HOME set
  - Download from: [Java SE 8 Downloads](https://www.oracle.com/java/technologies/javase/javase8-archive.html)
  - Or use OpenJDK 8 from [AdoptOpenJDK](https://adoptopenjdk.net/)
- **Eclipse IDE 2023-03** (March 2023 Release)
  - Download from: [eclipse.org/downloads](https://www.eclipse.org/downloads/)
- **Git** installed on Windows 11
- **Maven 3.6.0+** (or use Eclipse's embedded Maven)

## Step 1: Install Java 1.8

### 1.1 Download Java 1.8

1. Go to [Oracle Java SE 8 Archive](https://www.oracle.com/java/technologies/javase/javase8-archive.html)
2. Click "Download JDK" for Windows x64
3. Accept the license agreement
4. Download file: `jdk-8u401-windows-x64.exe` (or latest 8u version)

### 1.2 Install Java

1. Run the installer: `jdk-8u401-windows-x64.exe`
2. Click "Next" through the wizard
3. Default installation path: `C:\Program Files\Java\jdk1.8.0_401`
4. Complete the installation

### 1.3 Set JAVA_HOME Environment Variable

1. Press `Win + X` and select **System**
2. Click **Advanced system settings** on the left
3. Click **Environment Variables** button at the bottom
4. Under "System variables", click **New...**
5. Enter:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk1.8.0_401`
6. Click **OK**

### 1.4 Verify Java Installation

1. Open Command Prompt (press `Win + R`, type `cmd`, press Enter)
2. Type: `java -version`
3. Should display: `java version "1.8.0_401"` (or similar)

## Step 2: Install Eclipse 2023-03

### 2.1 Download Eclipse

1. Go to [eclipse.org/downloads](https://www.eclipse.org/downloads/)
2. Click **Download** on the Eclipse 2023-03 Release page
3. Select **Eclipse IDE for Java Developers**
4. Choose **Windows 64-bit** version
5. Download file: `eclipse-inst-jre-win64.exe`

### 2.2 Install Eclipse

1. Run the installer: `eclipse-inst-jre-win64.exe`
2. In the installer window:
   - Select **Eclipse IDE for Java Developers**
   - Click **Next**
3. Installation Folder:
   - Recommended: `C:\eclipse-2023-03`
   - Click **Next**
4. Click **Install**
5. Accept the license agreement
6. Wait for installation to complete (5-10 minutes)
7. Check "Launch Eclipse" checkbox and click **Finish**

### 2.3 Create Eclipse Workspace

1. When Eclipse starts, you'll see "Select a directory as workspace"
2. Recommended: `C:\eclipse-workspace`
3. Check "Use this as the default and do not ask again"
4. Click **Launch**

## Step 3: Configure Eclipse 2023-03 for Java 1.8

### 3.1 Configure Installed JREs

1. In Eclipse, go to **Window → Preferences**
2. In the left panel, expand **Java**
3. Click **Installed JREs**
4. You should see a JRE listed. If not:
   - Click **Add...**
   - Select **Standard VM**
   - Click **Next**
   - Click **Directory...** and browse to: `C:\Program Files\Java\jdk1.8.0_401`
   - Click **Open**
   - Click **Finish**
5. **Check the Java 1.8 JRE** to make it the default
6. Click **Apply and Close**

### 3.2 Configure Maven

1. In Eclipse, go to **Window → Preferences**
2. In the left panel, expand **Maven**
3. Click **Installations**
4. If Maven is not listed or you want to add your own:
   - Click **Add...**
   - Click **Directory...** and browse to your Maven folder
   - Example: `C:\apache-maven-3.9.0`
   - Click **Finish**
5. Select the Maven installation and click **Apply and Close**

### 3.3 Configure Compiler Compliance

1. In Eclipse, go to **Window → Preferences**
2. In the left panel, expand **Java**
3. Click **Compiler**
4. Set **Compiler compliance level** to **1.8**
5. Click **Apply and Close**

## Step 4: Clone the Repository in Eclipse

### Method 1: Using Git Integration (Recommended)

1. In Eclipse, go to **File → Import...**
2. In the import dialog, expand **Git**
3. Select **Projects from Git**
4. Click **Next**
5. Select **Clone URI**
6. Click **Next**
7. In the **URI** field, paste:
   ```
   https://github.com/mte325/fedorarepo-sync-app.git
   ```
8. Leave **User** and **Password** empty (public repository)
9. Click **Next**
10. Select **main** branch
11. Click **Next**
12. Choose **Destination** folder:
    - Example: `C:\eclipse-workspace\fedorarepo-sync-app`
13. Click **Finish**
14. When prompted "Import existing Eclipse projects?", select it
15. Click **Finish**

### Method 2: Command Line Clone

1. Open Command Prompt
2. Navigate to workspace:
   ```cmd
   cd C:\eclipse-workspace
   ```
3. Clone repository:
   ```cmd
   git clone https://github.com/mte325/fedorarepo-sync-app.git
   cd fedorarepo-sync-app
   ```
4. In Eclipse, go to **File → Open Projects from File System...**
5. Click **Directory...** and browse to cloned folder
6. Click **Finish**

## Step 5: Configure the Project

### 5.1 Set Project JRE to Java 1.8

1. Right-click on the project in **Project Explorer**
2. Select **Properties**
3. In the left panel, click **Java Build Path**
4. Click the **Libraries** tab
5. Look for "JRE System Library"
6. If it's not Java 1.8:
   - Select it and click **Remove**
   - Click **Add Library...**
   - Select **JRE System Library**
   - Click **Next**
   - Select **Workspace default JRE** (Java 1.8)
   - Click **Finish**
7. Click **Apply and Close**

### 5.2 Update Maven Configuration

1. Right-click on the project
2. Select **Maven → Update Project...**
3. Select the project checkbox
4. Click **OK**
5. Eclipse will download dependencies automatically

### 5.3 Build Project

1. Right-click on the project
2. Select **Project → Build Project**
3. Wait for build to complete (check Console tab)

## Step 6: Configure Application Settings

### 6.1 Edit Fedora Connection Settings

1. In **Project Explorer**, expand the project tree:
   ```
   src/main/resources/application.properties
   ```
2. Double-click to open the file
3. Update the Fedora connection details:
   ```properties
   fedora.url=http://your-fedora-server:8080/fedora
   fedora.username=your_username
   fedora.password=your_password
   fedora.api.version=5.1.1
   user.ids.file=config/user-ids.txt
   logging.level=INFO
   ```
4. Save: **Ctrl+S**

### 6.2 Add User IDs to File

1. In **Project Explorer**, find:
   ```
   config/user-ids.txt
   ```
2. Double-click to open
3. Add your IDs (one per line):
   ```
   id001
   id002
   id003
   test_object_001
   ```
4. Save: **Ctrl+S**

## Step 7: Run the Application

### Method 1: Direct Execution (Recommended)

1. In **Project Explorer**, expand:
   ```
   src/main/java/com/fedorasync/app/
   ```
2. Right-click on **FedoraSyncApplication.java**
3. Select **Run As → Java Application**
4. The application starts in the **Console** view at the bottom

### Method 2: Create a Run Configuration

1. Go to **Run → Run Configurations...**
2. Right-click **Java Application** in the left panel
3. Select **New** (or click the new icon)
4. Fill in the following:
   - **Name**: `FedoraSyncApp`
   - **Project**: Select your project
   - **Main class**: `com.fedorasync.app.FedoraSyncApplication`
5. Click **Run**

### Method 3: Run via Maven

1. Right-click on the project
2. Select **Run As → Maven build...**
3. In **Goals**, enter: `exec:java -Dexec.mainClass="com.fedorasync.app.FedoraSyncApplication"`
4. Click **Run**

## Step 8: Application Menu

After running, you'll see:

```
========================================
Fedora Repository Sync Application v1.0
========================================
Configuration loaded successfully
Fedora URL: http://your-fedora-server:8080/fedora
User IDs file: config/user-ids.txt

Please select an option:
1. Perform Repository Sync
2. Display Repository Information
3. Display User ID List
4. Exit
Enter your choice (1-4):
```

### Menu Options:

- **Option 1**: Syncs Fedora repository with your ID list
  - Retrieves all IDs from Fedora
  - Compares with your user ID list
  - Deletes IDs not in your list
  
- **Option 2**: Displays total objects in repository

- **Option 3**: Shows all IDs from your user list

- **Option 4**: Exits the application

Type the number and press **Enter** to select an option.

## Step 9: Building as JAR File

To create an executable JAR file:

1. Right-click on the project
2. Select **Run As → Maven build...**
3. In **Goals** field, enter: `clean package`
4. Click **Run**
5. JAR file is created at: `target/fedora-sync-app.jar`

To run the JAR from Command Prompt:

```cmd
java -jar target/fedora-sync-app.jar
```

## Step 10: View Logs

### Console Output

- Visible in Eclipse **Console** view
- Shows real-time INFO, WARN, and ERROR messages
- Shows all menu interactions

### Log File

- Location: `logs/fedora-sync-app.log`
- Created automatically after first run
- Contains all logging levels (DEBUG, INFO, WARN, ERROR)
- New log files created daily with rotation

To view the log file:
1. Use **File Explorer** and navigate to project folder
2. Open `logs/fedora-sync-app.log` with Notepad or text editor

## Troubleshooting for Eclipse 2023-03 & Java 1.8

### Issue: "Compliance level of existing Java project ... is not supported"

**Solution:**
1. Right-click project → **Properties**
2. Select **Project Facets** (if available)
3. Set Java version to **1.8**
4. Click **Apply and Close**

If "Project Facets" is not available, use Java Build Path instead (see Step 5.1).

### Issue: Maven dependencies not downloading

**Solution:**
1. Right-click project → **Maven → Update Project...**
2. Check **Force Update of Snapshots/Releases**
3. Click **OK**
4. Dependencies will re-download

### Issue: JRE System Library shows wrong Java version

**Solution:**
1. Right-click project → **Build Path → Configure Build Path**
2. Click **Libraries** tab
3. Select "JRE System Library"
4. Click **Remove**
5. Click **Add Library...**
6. Select **JRE System Library**
7. Click **Next**
8. Select **Workspace default JRE** (Java 1.8)
9. Click **Finish**
10. Click **Apply and Close**

### Issue: "Main class FedoraSyncApplication not found"

**Solution:**
1. Verify file exists: `src/main/java/com/fedorasync/app/FedoraSyncApplication.java`
2. Check package declaration at top of file: `package com.fedorasync.app;`
3. Right-click project → **Project → Clean**
4. Wait for rebuild
5. Try running again

### Issue: "Configuration file application.properties not found"

**Solution:**
1. Verify file exists: `src/main/resources/application.properties`
2. Right-click project → **Project → Clean**
3. Rebuild project
4. File should be copied to build output
5. Run again

### Issue: "User IDs file config/user-ids.txt not found"

**Solution:**
1. Verify file exists at project root: `config/user-ids.txt`
2. Check path in `application.properties`:
   ```properties
   user.ids.file=config/user-ids.txt
   ```
3. Use absolute path if needed:
   ```properties
   user.ids.file=C:/eclipse-workspace/fedorarepo-sync-app/config/user-ids.txt
   ```
4. Save and run again

### Issue: Connection timeout to Fedora server

**Solution:**
1. Verify Fedora URL in `application.properties` is correct
2. Check Fedora server is running and accessible
3. Test connectivity in Command Prompt:
   ```cmd
   ping your-fedora-server
   ```
4. Check firewall allows port 8080
5. Verify username and password are correct

### Issue: "Exception in thread main java.lang.NoClassDefFoundError"

**Solution:**
1. Ensure Maven dependencies downloaded successfully
2. Right-click project → **Maven → Update Project**
3. Build project: Right-click → **Project → Build Project**
4. Check for errors in **Console** view
5. Try running from JAR file instead

## Debugging in Eclipse 2023-03

### Set Breakpoints

1. Click on the line number in Java editor
2. A red dot appears (breakpoint set)
3. Run with **F11** (Debug mode)
4. Execution pauses at breakpoint
5. Use **Debug** view to inspect variables

### Enable Debug Logging

1. Edit `src/main/resources/logback.xml`
2. Change log level:
   ```xml
   <logger name="com.fedorasync" level="DEBUG"/>
   ```
3. Save and run
4. More detailed logs will appear in Console

### Common Shortcuts

- **Ctrl+Shift+F** - Format code
- **Ctrl+Shift+O** - Organize imports
- **Ctrl+F11** - Run as Java Application
- **F11** - Debug as Java Application
- **Ctrl+1** - Quick fixes
- **Ctrl+H** - Find and Replace
- **Alt+Shift+X, J** - Run as Java

## Eclipse 2023-03 + Java 1.8 Compatibility

All features confirmed working:
- ✅ Git integration for cloning
- ✅ Maven integration for builds
- ✅ Java 1.8 compiler support
- ✅ Project facets management
- ✅ Built-in debugger
- ✅ Console logging
- ✅ Code formatting and refactoring

## Project Structure in Eclipse

```
fedorarepo-sync-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fedorasync/
│   │   │       ├── app/
│   │   │       │   └── FedoraSyncApplication.java
│   │   │       ├── client/
│   │   │       │   └── FedoraRestClient.java
│   │   │       ├── service/
│   │   │       │   ├── FedoraRepositoryService.java
│   │   │       │   └── SyncService.java
│   │   │       ├── model/
│   │   │       │   └── SyncConfig.java
│   │   │       └── util/
│   │   │           ├── ConfigLoader.java
│   │   │           └── FileUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback.xml
│   └── test/ (optional)
├── config/
│   └── user-ids.txt
├── target/
│   └── fedora-sync-app.jar
├── pom.xml
├── .project
└── .classpath
```

## Next Steps

1. ✅ Configure your Fedora repository URL and credentials
2. ✅ Add your repository IDs to `config/user-ids.txt`
3. ✅ Run the application
4. ✅ Select **Option 1** to perform synchronization
5. ✅ Monitor console output for results
6. ✅ Check `logs/fedora-sync-app.log` for details

## Support & Resources

- **Fedora Documentation**: [duraspace.org/fedora](https://duraspace.org/fedora/)
- **Eclipse Documentation**: [eclipse.org/documentation](https://www.eclipse.org/documentation/)
- **Java 1.8 Documentation**: [Oracle Docs](https://docs.oracle.com/javase/8/docs/)
- **Maven Documentation**: [maven.apache.org](https://maven.apache.org/)

## Enjoy!

Your Fedora Repository Sync App is now running in Eclipse 2023-03 with Java 1.8! 🎉

For any issues, check the console output and `logs/fedora-sync-app.log` for detailed error messages.
