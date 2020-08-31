source 'parameters.sh'
#export servers=( "r146" "r147" "r148" "r149" )
for server in "${servers[@]}"
do
	echo $server
	ssh $server 'rm -rf ~/.KoPeMe/default/'
	ssh $server 'tmux kill-session -t test'
	#ssh -t $server "sudo reboot"
done


#cat~/.KoPeMe/default/de.precision.file.TestFileKoPeMe3/testAdd.xml | grep "value"

