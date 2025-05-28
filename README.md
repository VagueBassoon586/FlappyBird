# FlappyBird (Modified Version)

Modified version of [LeDuyKhang2004](https://github.com/LeDuyKhang2004)'s FlappyBird.

Check out the original repo here: [DACS by LeDuyKhang2004](https://github.com/LeDuyKhang2004/DACS)

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
