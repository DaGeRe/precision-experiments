#!/bin/bash

####SBATCH --nodes=1
####SBATCH --exclusive
#SBATCH --cpu-freq=high-high
#SBATCH --cpus-per-task=24
#SBATCH --ntasks=1

#export JAVA_HOME=/usr/jdk64/jdk1.8.0_112/
export PATH=/nfs/user/do820mize/maven/apache-maven-3.5.4/bin:/usr/jdk64/jdk1.8.0_112/bin/:/usr/lib64/qt-3.3/bin:/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/nfs/user/do820mize/pxz:/nfs/user/do820mize/tar-1.29/bin/bin:/nfs/user/do820mize/git/git-2.9.5/bin-wrappers

echo "Iterations: $ITERATIONS VMs: $VMS Workload: $WORKLOAD"
echo "Node: $SLURMD_NODENAME"
echo "Aliases: $SLURM_NODE_ALIASES"

java -version
echo $JAVA_HOME

nfsfolder=/nfs/user/do820mize/precision_results/$SLURM_JOB_ID"_"$SLURMD_NODENAME
mkdir $nfsfolder

export KOPEME_HOME=$HOME/kopeme_$SLURM_JOB_ID

if [ -z "$testcase" ]
then
	echo "No testcase given, using GenericAddTest"
	export testcase="de.precision.add.GenericAddTest"
fi
echo "Testcase: $testcase Buildtool: $BUILDTOOL"

mkdir $HOME/$SLURM_JOB_ID
export workloadsize=$WORKLOAD

repofile="/home/sc.uni-leipzig.de/do820mize/.m2/repository/de/dagere/kopeme/kopeme-junit/0.11-SNAPSHOT/kopeme-junit-0.11-SNAPSHOT.jar"
diff=$(cmp $repofile /nfs/user/do820mize/libs/KoPeMe/kopeme-junit/target/kopeme-junit-0.11-SNAPSHOT.jar)
if [ ! -f $repofile ] || [ ! -z "$diff" ]
then
	srun --export=PATH,JAVA_HOME \
		-o $nfsfolder/kopeme.txt \
		--chdir=/nfs/user/do820mize/libs/KoPeMe mvn jar:jar install:install
fi

PROJECT_FOLDER=$HOME/$SLURM_JOB_ID/project/
cp -R /nfs/user/do820mize/precision-experiment/ $PROJECT_FOLDER

for (( i=1; i<=$VMS; i++))
do
	echo $i
	writefile=$HOME/$SLURM_JOB_ID/"out_"$i.txt
	echo "Write to $writefile"
	if [[ "$BUILDTOOL" == "mvn" ]]
	then
		srun --export=PATH,JAVA_HOME,https_proxy,http_proxy,HTTP_PROXY,HTTPS_PROXY,KOPEME_HOME,workloadsize \
			-o $writefile \
			--chdir=$PROJECT_FOLDER \
			mvn \
			clean test -Dtest=$testcase
	else
		srun --export=PATH,JAVA_HOME,https_proxy,http_proxy,HTTP_PROXY,HTTPS_PROXY,KOPEME_HOME,workloadsize \
			-o $writefile \
			$PROJECT_FOLDER/./gradlew --no-daemon \
			-p $PROJECT_FOLDER clean test --tests $testcase
	fi
	mv $HOME/kopeme_$SLURM_JOB_ID/de.peass/precision-experiment/$testcase $nfsfolder/"wl_"$workloadsize"_"$i
	mv $writefile $nfsfolder/"out_"$i.txt
	ls $HOME/kopeme_$SLURM_JOB_ID/
	ls $nfsfolder
done

#ls $HOME/$SLURM_JOB_ID
#echo "Moving to nfs"
#mv $HOME/$SLURM_JOB_ID /nfs/user/do820mize/precision_results/$SLURM_JOB_ID



