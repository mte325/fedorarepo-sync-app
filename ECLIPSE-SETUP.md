# Eclipse IDE Setup Guide

Complete step-by-step guide to download, configure, and run the Fedora Repository Sync App in Eclipse IDE on Windows 11.

## Prerequisites

Before starting, ensure you have:
- Java 11 or higher installed with JAVA_HOME set
- Eclipse IDE for Java Developers (2023-09 or later recommended)
- Git installed on Windows 11
- Maven installed (or use Eclipse's embedded Maven)

## Step 1: Install Eclipse IDE

1. Download Eclipse from [eclipse.org](https://www.eclipse.org/downloads/)
2. Choose "Eclipse IDE for Java Developers"
3. Run the installer and follow the wizard
4. During installation:
   - Select installation folder (e.g., `C:\eclipse`)
   - Choose Java runtime environment (select JDK 11+)
5. Click "Install" and wait for completion
6. Launch Eclipse

## Step 2: Configure Eclipse for Maven

1. In Eclipse, go to **Window → Preferences**
2. Expand **Maven** in the left panel
3. Select **Installations**
4. Click **Add** if Maven is not listed
5. Browse to your Maven installation folder (e.g., `C:\apache-maven-3.9.0`)
6. Click **Finish**
7. Select the Maven version you added and click **Apply and Close**

## Step 3: Clone the Repository in Eclipse

### Method 1: Using Git Integration (Recommended)

1. In Eclipse, go to **File → Import...**
2. Expand **Git** and select **Projects from Git**
3. Click **Next**
4. Select **Clone URI**
5. Click **Next**
6. In the "URI" field, enter:
   ```
   https://github.com/mte325/fedorarepo-sync-app.git
   ```
7. Click **Next** (leave authentication empty for public repo)
8. Select **main** branch
9. Click **Next**
10. Choose destination folder (e.g., `C:\workspace\fedorarepo-sync-app`)
11. Click **Finish**
12. When prompted, select **Import existing Eclipse projects**
13. Click **Next** then **Finish**

### Method 2: Using Command Line

1. Open Command Prompt or PowerShell
2. Navigate to your workspace folder:
   ```cmd
   cd C:\eclipse-workspace
   ```
3. Clone the repository:
   ```cmd
   git clone https://github.com/mte325/fedorarepo-sync-app.git
   cd fedorarepo-sync-app
   ```
4. In Eclipse, go to **File → Open Projects from File System...**
5. Click **Directory...** and browse to the cloned folder
6. Click **Finish**

## Step 4: Configure the Project

### 1. Convert to Maven Project (if needed)

1. Right-click on the project in Project Explorer
2. Select **Configure → Convert to Maven Project**
3. Accept the default settings and click **Finish**

### 2. Update Project Properties

1. Right-click on the project → **Properties**
2. Select **Project Facets**
3. Ensure "Java" is selected with version 11+
4. Click **Apply and Close**

### 3. Clean and Build

1. Right-click on the project
2. Select **Maven → Update Project...**
3. Select the project and click **OK**
4. Eclipse will download dependencies automatically

## Step 5: Configure Application Settings

### 1. Edit application.properties

1. In Project Explorer, expand the project tree:
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
4. Save the file (Ctrl+S)

### 2. Add User IDs

1. In Project Explorer, find `config/user-ids.txt`
2. Double-click to open
3. Add your IDs (one per line):
   ```
   id001
   id002
   id003
   ```
4. Save the file

## Step 6: Run the Application in Eclipse

### Method 1: Direct Execution

1. Right-click on `FedoraSyncApplication.java` in Project Explorer
2. Select **Run As → Java Application**
3. The application will start in the Eclipse Console view

### Method 2: Create a Run Configuration

1. Go to **Run → Run Configurations...**
2. Click **Java Application** in the left panel
3. Click **New** (top-left icon)
4. In the "Main" tab:
   - Name: `FedoraSyncApp`
   - Project: Select your project
   - Main class: `com.fedorasync.app.FedoraSyncApplication`
5. Click **Apply** then **Run**

### Method 3: Debug Mode

1. Right-click `FedoraSyncApplication.java`
2. Select **Debug As → Java Application**
3. Use Eclipse's debugger to step through code

## Step 7: Build as JAR (Optional)

To create an executable JAR file:

1. Right-click on the project
2. Select **Run As → Maven build...**
3. In "Goals" field, enter:
   ```
   clean package
   ```
4. Click **Run**
5. The JAR file will be created in `target/fedora-sync-app.jar`

## Step 8: Run the Application

### From Eclipse Console

After running the application, you'll see:
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

Type `1` and press Enter to start synchronization.

### From Command Line (After Building JAR)

```cmd
cd C:\path\to\fedorarepo-sync-app
java -jar target/fedora-sync-app.jar
```

## Troubleshooting in Eclipse

### Issue: Maven dependencies not downloading

**Solution:**
1. Right-click project → **Maven → Update Project...**
2. Check "Force Update of Snapshots/Releases"
3. Click **OK**

### Issue: "Main class not found"

**Solution:**
1. Ensure `FedoraSyncApplication.java` is in:
   ```
   src/main/java/com/fedorasync/app/
   ```
2. Check package declaration at top of file:
   ```java
   package com.fedorasync.app;
   ```

### Issue: Configuration file not found

**Solution:**
1. Ensure `application.properties` is in:
   ```
   src/main/resources/
   ```
2. Right-click project → **Build Path → Clean All Output Folders**
3. Project will rebuild automatically

### Issue: User IDs file not found

**Solution:**
1. Verify `config/user-ids.txt` exists in project root
2. Check path in `application.properties`:
   ```properties
   user.ids.file=config/user-ids.txt
   ```
3. Use absolute path if needed:
   ```properties
   user.ids.file=C:/workspace/fedorarepo-sync-app/config/user-ids.txt
   ```

### Issue: Connection timeout to Fedora

**Solution:**
1. Verify Fedora server is running
2. Check URL in `application.properties`
3. Test connectivity with:
   ```cmd
   ping your-fedora-server
   ```
4. Check firewall settings

## Debugging Tips

### Enable Debug Logging

Edit `src/main/resources/logback.xml`:
```xml
<logger name="com.fedorasync" level="DEBUG"/>
```

### View Logs in Eclipse

The console will display real-time logs. Check `logs/fedora-sync-app.log` for persistent logs.

### Set Breakpoints

1. Click on the line number in code editor
2. A breakpoint will be set (red dot appears)
3. Run as Debug → step through execution
4. Use Debug view to inspect variables

## Project Structure in Eclipse

```
fedorarepo-sync-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fedorasync/
│   │   │       ├── app/
│   │   │       ├── client/
│   │   │       ├── service/
│   │   │       ├── model/
│   │   │       └── util/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback.xml
│   └── test/ (if applicable)
├── config/
│   └── user-ids.txt
├── pom.xml
├── README.md
└── .project
```

## Common Shortcuts in Eclipse

- **Ctrl+Shift+F** - Format code
- **Ctrl+Shift+O** - Organize imports
- **Ctrl+F11** - Run application
- **F11** - Debug application
- **Ctrl+Alt+H** - Open call hierarchy
- **Ctrl+1** - Quick fix suggestions

## Next Steps

1. Configure your Fedora repository credentials
2. Add your IDs to `config/user-ids.txt`
3. Run the application
4. Monitor console output and logs
5. Review results in the console menu

## Support

For issues:
- Check `logs/fedora-sync-app.log` for errors
- Review Eclipse Error Log (Window → Show View → Error Log)
- Consult README.md in the project
- Check Fedora documentation

Enjoy using the Fedora Repository Sync App in Eclipse! 🎉
