import SwiftUI
import shared

struct ContentView: View {

    @State var imagePickerViewModel = ImagePickerViewModel()
    
	var body: some View {
        Button("Gallery", action: { self.imagePickerViewModel.isPresented.toggle() })
            .sheet(isPresented: $imagePickerViewModel.isPresented) {
                ImagePickerView(model: self.$imagePickerViewModel)
            }
            .onReceive(imagePickerViewModel.pickedImagesSubject) { info -> Void in
                withAnimation {
                    let image = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
                    let data = image.jpegData(compressionQuality: 50)
                    uploadImage(data: data)
                }
            }
	}
    
    func uploadImage(data: Data?) {
        let fileName = NSUUID().uuidString + ".jpg"
        let identityPoolId = "us-east-1:c42ab2f1-5f5c-406d-8c75-5ffb25a697f1"
        let endPoint = "golfpoker-demo.s3.amazonaws.com"
        let bucketName = "golfpoker-demo"
        let folderName = "Folder"
        let awsImageUploader = AWSImageUploader(awsIdentityPoolId: identityPoolId, awsBucketName: bucketName, awsFolderName: folderName, awsRegion: AWSRegion.usEast1)
        awsImageUploader.addConfiguration(maxRetry: nil, timeOutIntervalForRequest: nil, timeoutIntervalForResource: nil)
        awsImageUploader.uploadImage(data: data!, listener: test(), fileName: fileName, fileType: ".image")
        
    }

}

class test: ImageUploadListener {
    func imageUploadProgress(percentage: Float) {
        print(percentage)
    }
    
    func imageUploadCompleted(url: String) {
     print(url)
    }
    
    func imageUploadFailure(message: String) {
        print(message)
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
	ContentView()
	}
}
