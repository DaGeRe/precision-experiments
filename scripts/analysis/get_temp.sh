watch --interval 1 "(date +%s | tr -d '\n' && echo -n ' ' && sensors | grep Core | awk '{print \$3}\\' | tr '\n' ' ' && echo "") >> temp.csv"
