if [ -z "$buildtool" ]
then
	buildtool="gradle"
fi
echo "Buildtool: $buildtool Testcase: $testcase"

for i in {1..1}
do
	sbatch --partition=galaxy-low-prio \
		--nice=1 \
		--time=10-0 \
		--output=/nfs/user/do820mize/processlogs/precision/"%j".out \
		--export=VMS=500,ITERATIONS=20000,WORKLOAD=$i,BUILDTOOL=$buildtool,testcase=$testcase executeSizeEvolution.sh
done
