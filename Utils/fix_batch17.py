import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/OemCommandSender.java")
t = p.read_text(encoding="utf-8", errors="replace")
old = """        } catch (Exception | LinkageError unused5) {
        }
    }
}"""
new = """        } catch (Exception | LinkageError unused5) {
            return "Не удалось отправить команду автомобилю";
        }
    }
}"""
assert old in t, "MISS"
p.write_text(t.replace(old, new, 1), encoding="utf-8")
print("OemCommandSender fixed")
