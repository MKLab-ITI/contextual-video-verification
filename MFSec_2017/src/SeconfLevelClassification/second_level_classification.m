function second_level_classification(histogram_feat_file, lables, folds)
        %% histogram_feat_file contains all histogram vectors of the videos
        %% labels contains 0 for real 1 for fake video
        %% folds the number of folds for cross validation 
         root_path = [pwd '/'];
        %%% SVM parameters        
         best_C = 8;
         best_gamma = 1;
         
         data = dlmread(histogram_feat_file);
         data_labels = dlmread(lables);

        indices  = crossvalind('Kfold', data_labels, folds);
        dlmwrite('indices.txt',indices);      

        for i=1:folds
                  test_auth = (indices == i); train_auth = ~test_auth;
                  trainFold = data(train_auth,:);    
                  trainLabelFold = data_labels(train_auth,:);

                  testFold = data(test_auth,:);
                  testLabelfold = data_labels(test_auth,:);
                  dlmwrite([root_path 'labels_fold' num2str(i) '.txt'],testLabelfold);

                  model = svmtrain(trainLabelFold, trainFold,sprintf('-t 2 -b 1 -c %f -g %f', best_C, best_gamma));
                  [~, accuracy, prob] = svmpredict(testLabelfold, testFold, model,'-b 1');
                  dlmwrite([root_path 'prob_fold' num2str(i) '.txt'],prob);
        end
end
