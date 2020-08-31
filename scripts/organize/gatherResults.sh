source 'servers.sh'
#export servers=( "r146" "r147" "r148" "r149" )
for server in "${servers[@]}"
do
	echo $server
	mkdir -p results/$server
	if ssh $server '[ -d ~/.KoPeMe/default/repetition_100/ ]' 
	then
	  mkdir -p results/$server
	  scp -r $server:~/.KoPeMe/default/* results/$server/
	  #ssh $server 'rm -rf ~/.KoPeMe/default/'
	  #ssh $server 'tmux kill-session -t test'
  	  cat results/$server/*.xml | grep "<result" -A 1 | grep value | tr -d "<value/>" | sort -n
  	else
	  ssh -t $server cat ~/.KoPeMe/default/$test/*.xml | grep "<result" -A 1 | grep "value" | tr -d "value<>/" | sort -n | tee results/$server/data.csv
	  avg=$(cat results/$server/data.csv | awk '{ sum += $1; n++ } END { if (n > 0) print sum / n; }')
	  tempavg=$(ssh $server sensors -u | grep "_input" | awk '{ sum += $2; n++ } END { if (n > 0) print sum / n; }')
	  echo "Average: $avg Temp-Average: $tempavg"
	  echo "$avg;$tempavg" >> data.csv
	  #ssh $server sensors -u | grep "_input" | awk '{ sum += $2; n++ } END { if (n > 0) print sum / n; }'
  	  #ssh $server cat /proc/meminfo | head -n 4
  	fi
done


#cat~/.KoPeMe/default/de.precision.file.TestFileKoPeMe3/testAdd.xml | grep "value"

