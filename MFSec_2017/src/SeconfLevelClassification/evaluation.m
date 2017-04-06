function [mPrec, mRecall, mAcc, mF1 ] = evaluation(folds)
            %%% compute 
           % clear all; fclose all; clc;
            root_path = [pwd '/'];

            precision_all = 0;
            recall_all = 0;
            f1_all = 0;
            acc_all = 0;
            
            fid =  fopen('EvaluationResults.txt','w');
            fprintf(fid,'%s\t', 'True_Possitive');
            fprintf(fid,'%s\t', 'False_Possitive');
            fprintf(fid,'%s\t', 'True_Negative');
            fprintf(fid,'%s\t', 'False_Negative');
            fprintf(fid,'%s\t', 'Precision');
            fprintf(fid,'%s\t', 'Recall');
            fprintf(fid,'%s\t', 'Accuracy');
            fprintf(fid,'%s\n', 'F1-measure');
            
            for fold =1 : folds
                prob = dlmread([root_path 'prob_fold' num2str(fold) '.txt']);
                gt = dlmread([root_path 'labels_fold' num2str(fold) '.txt']);       
         
                cnt_t = 0 ;
                cnt_n = 0;
                lb = size(size(prob,1));
                     for i=1:size(prob,1)

                        if (prob(i,1)<0.5)
                            lb(i) = 0;
                        else
                            lb(i) = 1;
                        end

                        if (lb(i) == gt(i))
                            cnt_t = cnt_t + 1;
                        else
                            cnt_n = cnt_n + 1;
                        end

                     end
                     
                    predict = lb';
                    true_pos = 0;
                    true_neg = 0;
                    false_neg = 0;
                    false_pos = 0;
                    for i=1:size(predict,1)
                        if (predict(i,1) == gt(i,1))
                           if (predict(i,1) == 1)
                               true_pos = true_pos + 1;
                           else
                               true_neg = true_neg + 1;
                           end
                        else
                            if (predict(i,1) == 1)
                                false_neg = false_neg + 1;
                            else
                                false_pos = false_pos + 1;
                            end
                        end
                    end
                    if (true_pos == 0)
                        precision = 0;
                        recall = 0;
                    else
                         precision = true_pos/(true_pos + false_pos);
                         recall = true_pos/(true_pos +false_neg);
                    end
                   
                    precision_all = precision_all + precision;                  
                    recall_all = recall_all + recall;
                    if ((precision == 0)||(recall)==0)
                          f1 = 0;
                    else
                          f1 = 2* (precision*recall)/(precision + recall)
                    end
                  
                    f1_all = f1_all + f1;
                    disp('true');
                    cnt_t
                    disp('false');
                    cnt_n
                    acc = (cnt_t * 100) / size(prob,1)
                    acc_all = acc_all + acc;
                    % TP TN FP FN Precision Recall Accuracy F1-measure 
                   % fprintf(fid,'Run ID %s ', [runid '_' num2str(fold) ':']);
                    fprintf(fid,'%d\t', true_pos);
                    fprintf(fid,'%d\t', true_neg);
                    fprintf(fid,'%d\t', false_pos);
                    fprintf(fid,'%d\t', false_neg);
                    fprintf(fid,'%2.4f\t', precision);
                    fprintf(fid,'%2.4f\t', recall);
                    fprintf(fid,'%2.4f\t', acc);
                    fprintf(fid,'%2.4f\n', f1);
            end
            fprintf(fid,'%s\t','');
            fprintf(fid,'%s\t','');
            fprintf(fid,'%s\t','');
            fprintf(fid,'%s\t','');
            fprintf(fid,'%2.4f\t', precision_all/folds);
            fprintf(fid,'%2.4f\t', recall_all/folds);
            fprintf(fid,'%2.4f\t', acc_all/folds);
            fprintf(fid,'%2.4f', f1_all/folds);
            fclose(fid);
end
