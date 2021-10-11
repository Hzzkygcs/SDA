from django.http import HttpResponse, HttpResponseNotFound
import os, time

def index_boom(request):
    
    
    file_location = os.path.dirname(__file__) + request.path
    file_location = file_location.replace("\\", "/")

    try:    
        with open(file_location, 'r') as f:
           file_data = f.read()

        # sending response 
        response = HttpResponse(file_data, content_type='application/octet-stream')
        response['Content-Disposition'] = f'attachment; filename="{os.path.basename(file_location)}"'
        
    except UnicodeDecodeError as e:
        time.sleep(2)
        with open(file_location, 'rb') as f:
           file_data = f.read()

        # sending response 
        response = HttpResponse(file_data, content_type='application/octet-stream')
        response['Content-Disposition'] = f'attachment; filename="{os.path.basename(file_location)}"'

    except IOError as e:
        # handle file not exist case here
        response = HttpResponseNotFound('<h1>File not exist</h1>' + file_location + "<br/>" + str(e))

    return response
