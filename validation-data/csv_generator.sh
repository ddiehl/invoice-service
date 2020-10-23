#!/usr/bin/env bash

function comma_delimited {
  local d=,;
  echo -n "$1";
  shift;
  printf "%s" "${@/#/$d}";
  echo '';
}

function new_file {
  file_name=invoice_data
  if [[ -e $file_name.csv || -L $file_name.csv ]] ; then
    i=1;
    while [[ -e "${file_name}_${i}".csv || -L "${file_name}_${i}".csv ]] ; do
      let i++;
    done
    file_name="${file_name}_${i}";
  fi
  echo -n $file_name;
}

function gen_supplier_id {
  for i in {0..10}; do
    suppliers[$i]="supplier_$(($i+1))";
  done

  size=${#suppliers[@]};
  index=$(($RANDOM % $size));
  echo -n ${suppliers[$index]};
}
function gen_invoice_id {
  echo -n $(uuidgen);
#  echo -n $(($RANDOM % 100)); # if you want to generate duplicates
}
function gen_invoice_date {
  days=$(($RANDOM % 90));
  echo -n $(date -v-${days}d '+%Y-%m-%d');
}
function gen_invoice_amount {
  echo -n "$(( ($RANDOM % 10000) / ($RANDOM % 5 + 1) )).$(($RANDOM % 100))";
}
function gen_terms {
  terms[0]="30";
  terms[1]="60";
  terms[2]="90";
  terms[3]="120";

  size=${#terms[@]};
  index=$(($RANDOM % $size));
  echo -n ${terms[$index]};
}
function gen_payment_data {
  # roll for payment on invoice 1 in 10
  if [[ $(($RANDOM % 10)) -eq 0 ]] ; then
    payment_date=$(date -v +$2d -jf %F $1 +%F);
    # roll for payment on invoice 1 in 10
    if [[ $(($RANDOM % 5)) -eq 0 ]] ; then
      payment_amount=$(echo $3 - $(($RANDOM % 100)) | bc);
    else
      payment_amount=$3;
    fi
  else
    payment_date='';
    payment_amount='';
  fi
  echo -n "${payment_date},${payment_amount}";
}

file_name=$(new_file);

echo "Generating invoice data in file: $file_name.csv";

# add headers
comma_delimited "Supplier Id" "Invoice Id" "Invoice Date" "Invoice Amount" "Terms" "Payment Date" "Payment Amount" >> $file_name.csv;

rows=${1:-50};
for (( i=0; i<$rows; i++ )); do
  supplier_id=$(gen_supplier_id);
  invoice_id=$(gen_invoice_id);
  invoice_date=$(gen_invoice_date);
  invoice_amount=$(gen_invoice_amount);
  terms=$(gen_terms);
  payment_data=$(gen_payment_data $invoice_date $terms $invoice_amount);

  comma_delimited $supplier_id $invoice_id $invoice_date $invoice_amount $terms $payment_data >> $file_name.csv;
done