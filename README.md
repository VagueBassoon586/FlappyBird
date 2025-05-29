# FlappyBird remake

Modified version of [LeDuyKhang2004](https://github.com/LeDuyKhang2004)'s FlappyBird.

Check out the original repo here: [DACS by LeDuyKhang2004](https://github.com/LeDuyKhang2004/DACS)

The original code and this version started out about 95% similar. However, we eventually diverged — mainly due to differences in how we handled the user database.

- **LeDuyKhang2004** used a localhost server: a [SQL Server 2022](microsoft.com/en-us/sql-server/sql-server-2022) instance. This was required for his CS course project.
- **This version** opts for simplicity: it uses a `flappybird.db` file by SQLite to store user data locally.

As a result, the final versions are significantly different, especially in the database integration and user system logic.

---

## 🚀 How to Run This Game

You can either:

**Download the ZIP**  
or  
**Clone the repository** using this command:

```bash
git clone https://github.com/VagueBassoon586/FlappyBird.git
```

---

## If You Know How to Run a Java Project

Navigate to:

```
/src/FLB/App.java
```

...and run it using your preferred IDE or terminal.

---

## If You Don’t Know How to Run a Java Project

1. 💻 **Open your terminal**
2. Navigate to the 📁 **root folder** of the FlappyBird project:

```bash
cd /path/to/the/root/project/folder
```

3. **Run the following command depending on your OS:**

### 🪟 For **Windows** users

```bash
javac -cp "lib/*" -d bin src/FLB/*.java && java -cp "bin;lib/*" FLB.App
```

### 🍎 For **macOS/Linux** users

```bash
javac -cp "lib/*" -d bin src/FLB/*.java && java -cp "bin:lib/*" FLB.App
```

> ⚠️ The only difference is the `;` (Windows) vs `:` (macOS/Linux) in the classpath.

---

## 🎉 Enjoy the game!
