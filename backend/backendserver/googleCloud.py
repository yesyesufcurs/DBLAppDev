# Source: https://cloud.google.com/vision/docs/ocr
def detect_text(content):
    """Detects text in the file."""
    from google.cloud import vision
    client = vision.ImageAnnotatorClient()

    image = vision.Image(content=content)

    response = client.text_detection(image=image)
    texts = response.text_annotations

    foundText = ""
    for text in texts:
        foundText += text.description + " "

    if response.error.message:
        raise Exception(
            '{}\nFor more info on error messages, check: '
            'https://cloud.google.com/apis/design/errors'.format(
                response.error.message))
    return foundText