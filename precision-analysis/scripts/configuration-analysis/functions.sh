function getTestIndex {
	statisticalTest=$1
	case "$statisticalTest" in
		"MEAN")
			index=9
			;;
		"TTEST")
			index=13
			;;
		"CONFIDENCE")
			index=21
			;;
		"MANNWHITNEY")
			index=25
			;;
	esac
	echo $index
}
