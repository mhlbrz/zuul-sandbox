#!/bin/bash

# 3 Benchmarks
# size=1mb, n=1, t=0, C=100, N=500
# size=10mb, n=1, t=0, C=100, N=500
# size=100mb, n=10, t=50ms, C=50, N=50

run_benchmark() {
  size=$1
  n=$2
  t=$3
  C=$4
  N=$5
  rc=$6
  name=test-s$size-n$n-t$t-C$C-N$N-r$rc

  printf "start benchmark %s\n" $name
  mkdir -p $name
  # start cpu/mem logging
  printf "start cpu/mem logging\n"
  ssh aws-zuul-edge 'sar -u 1 > cpu &'
  ssh aws-zuul-edge 'sar -r 1 > mem &'
  # start benchmark
  printf "start ab\n" $name
  ab -n $N -c $C http://$HOST:8080/server/size-bytes/$size/nchunks/$n/interval-ms/$t/reconcile/$rc > $name/bench
  # stop sar
  printf "ab done, copy logs\n"
  ssh aws-zuul-edge 'pkill -f "sar"'
  scp aws-zuul-edge:/home/ubuntu/cpu $name/cpu
  scp aws-zuul-edge:/home/ubuntu/mem $name/mem
}

HOST='ec2-3-133-121-0.us-east-2.compute.amazonaws.com'
#HOST='localhost'

# Benchmark #1
run_benchmark 1000000 1 0 100 500 false
run_benchmark 1000000 1 0 100 500 true

# Benchmark #2
run_benchmark 10000000 1 0 100 500 false
run_benchmark 10000000 1 0 100 500 true

# Benchmark #3
run_benchmark 100000000 10 50 50 50 false
run_benchmark 100000000 10 50 50 50 true
