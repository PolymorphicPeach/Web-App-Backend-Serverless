# https://docs.aws.amazon.com/lambda/latest/dg/python-image.html#python-image-instructions

FROM public.ecr.aws/lambda/python:3.8

# Copy requirements.txt
COPY requirements.txt ${LAMBDA_TASK_ROOT}

# Install the specified packages
RUN pip install -r requirements.txt

# Copy function code
COPY aerial-classifier.py ${LAMBDA_TASK_ROOT}
COPY static ${LAMBDA_TASK_ROOT}/static

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "aerial-classifier.handler" ]