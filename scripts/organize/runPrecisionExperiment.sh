
source 'parameters.sh'

i=0
#while true
#do
for server in "${servers[@]}"
do
#	arraylength=${#servers[@]}
#	echo "Servers: $arraylength"
#	serverindex=$(($i%$arraylength))
#	server=${servers[$serverindex]}
	if ssh -t $server tmux ls | grep test
	then
		echo "Server $server in use"
	else
		ssh $server 'mkdir -p workspaces/dissworkspace'
		ssh $server "cd workspaces/dissworkspace/peass && git pull && mvn clean install -DskipTests=true"

		mkdir -p results/$i
		rsync -avz --exclude .git --exclude build \
		       	--exclude scripts/organize/ \
			--exclude results/ \
			--exclude bin/ \
			../../../precision-experiment/ results/$i
		rsync -avz --exclude .git --exclude build \
			--exclude scripts/organize/ \
			--exclude results/ ../../../precision-experiment/ \
			--exclude bin/ \
			$server:workspaces/dissworkspace/precision-experiment/
		#ssh -t $server 'cd workspaces/dissworkspace/precision-experiment/scripts/ && ./PrepareMeasurements.sh'
		ssh -t $server 'cd workspaces/dissworkspace/precision-experiment/scripts/ && tmux new-session -d -s test && tmux send-keys '$skript'" "'$test' C-m'
	fi
	i=$(($i+1))
	echo "Index: $i"
done
