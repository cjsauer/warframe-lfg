aws autoscaling set-desired-capacity --auto-scaling-group-name $BASTION_AUTOSCALE_NAME --desired-capacity 1
aws autoscaling set-desired-capacity --auto-scaling-group-name $TX_AUTOSCALE_NAME --desired-capacity 1
