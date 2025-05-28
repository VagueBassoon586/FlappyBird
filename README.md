# FlappyBird remake

Modified version of [LeDuyKhang2004](github.com/LeDuyKhang2004)'s FlappyBird.

Check out the original repo here: [DACS by LeDuyKhang2004](github.com/LeDuyKhang2004/DACS)

Our code was initially 95% similar. However, we went different ways with our code while working with the user's database. LeDuyKhang2004 need to use a local [SQL Server 2022](microsoft.com/en-us/sql-server/sql-server-2022) (it is one of the requirements; he needs to submit this game as a project for his CS course). Meanwhile, I prefer a simpler method: store user data in a .db file using SQLite. After our databases were finished, the codes were too different.

---

## 🚀 How to Run This Game

You can either:

**Download the ZIP**  
or  
**Clone the repository** using this command:

```bash
git clone https://github.com/LeDuyKhang2004/DACS
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
