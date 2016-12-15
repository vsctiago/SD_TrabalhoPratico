package MulticastPackage;

import java.util.ArrayList;

public class WorkFileList extends Thread{

    private ArrayList<FileList> fileList;
    private FileList tempFileList;

    public WorkFileList(ArrayList<FileList> fileList, FileList temFileList) {
        this.fileList = fileList;
        this.tempFileList = temFileList;
    }

    @Override
    public void run() {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).getClientName().equals(tempFileList.getClientName())) {
                fileList.remove(i);
                fileList.add(tempFileList);
                return;
            }
        }
        fileList.add(tempFileList);
    }
}
