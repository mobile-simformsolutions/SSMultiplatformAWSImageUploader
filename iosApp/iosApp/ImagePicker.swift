//
//  ImagePicker.swift
//  iosApp
//
//  Created by Mohammed Hanif on 11/07/21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import Combine

struct ImagePickerView : UIViewControllerRepresentable {

    @Binding var model: ImagePickerViewModel

    typealias UIViewControllerType = UIImagePickerController

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: UIViewControllerRepresentableContext<Self>) -> UIImagePickerController {
        let controller = UIImagePickerController()
        controller.delegate = context.coordinator
        controller.allowsEditing = false
        controller.mediaTypes = ["public.image"]
        controller.sourceType = .photoLibrary
        return controller
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: UIViewControllerRepresentableContext<ImagePickerView>) {
        // run right after making
    }

    class Coordinator : NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

        var parentView: ImagePickerView

        init(_ parentView: ImagePickerView) {
            self.parentView = parentView
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parentView.model.isPresented = false
        }

        func imagePickerController(_ picker: UIImagePickerController,
                                          didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            parentView.model.pickedImagesSubject.send(info)
            parentView.model.isPresented = false
        }
    }
}

struct ImagePickerViewModel {
    var isPresented: Bool = false
    let pickedImagesSubject = PassthroughSubject<[UIImagePickerController.InfoKey: Any], Never>()
}
